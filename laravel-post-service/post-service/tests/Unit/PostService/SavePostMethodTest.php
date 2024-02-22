<?php

namespace Tests\Unit\PostService;

use App\Kafka\PostProducer;
use App\Services\PostService;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Redis;
use Mockery;
use PHPUnit\Framework\TestCase;
use Ramsey\Uuid\Uuid;

class SavePostMethodTest extends TestCase
{

    public function test_save_post_should_return_true_after_saving_a_post_to_redis_and_produce_a_message(): void
    {

        $producer = Mockery::mock(PostProducer::class);
        $producer->shouldReceive('produce')->once();

        Redis::shouldReceive('lPush')->once()
            ->andReturn(1);

        Log::shouldReceive('info')->once();

        $service = new PostService();
        $username = 'alimzhan';
        $imageUrl = 'https://example.com/image.jpg';
        $caption = 'Test caption';

        $result = $service->savePost($username, $imageUrl, $caption, $producer);

        $this->assertTrue($result);
    }

    public function test_save_post_should_generate_uuid_for_a_new_post()
    {

        $producer = Mockery::mock(PostProducer::class);
        $producer->shouldReceive('produce')->once();

        Log::shouldReceive('info')->once();

        $uuid = Mockery::mock(Uuid::class);
        $uuid->shouldReceive('uuid4')->once()
            ->andReturn(Mockery::mock()
                ->shouldReceive('toString'
                )
                ->once()
                ->getMock()
            );

        $service = new PostService();

        $username = 'alimzhan';
        $imageUrl = 'https://example.com/image.jpg';
        $caption = 'Test caption';
        $post_key = "$username:$uuid";

        Redis::shouldReceive('lPush')->once()
            ->andReturnTrue();

        Redis::shouldReceive('exists')->once()
            ->with($post_key)
            ->andReturnTrue();

        $service->savePost($username, $imageUrl, $caption, $producer);

        $this->assertTrue(Redis::exists($post_key));
    }
}
