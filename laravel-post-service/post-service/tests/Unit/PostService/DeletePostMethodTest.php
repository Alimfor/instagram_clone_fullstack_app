<?php

namespace Tests\Unit\PostService;

use App\Exceptions\NotAllowedException;
use App\Exceptions\ResourceNotFoundException;
use App\Kafka\PostProducer;
use App\Services\PostService;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Redis;
use Mockery;
use PHPUnit\Framework\TestCase;

class DeletePostMethodTest extends TestCase
{

    public function test_delete_post_should_return_true_after_deleting()
    {

        $producer = Mockery::mock(PostProducer::class);
        $producer->shouldReceive('produce')->once();

        Log::shouldReceive('info')->twice();

        $username = 'alimzhan';
        $postId = '8032a848-f765-4e63-8fbb-11acde9fefc7';
        $postKey = $username . ':' . $postId;

        Redis::shouldReceive('del')
            ->with($postKey)
            ->once();

        Redis::shouldReceive('lRange')
            ->with($postKey, 0, -1)
            ->once()
            ->andReturn([$this->getDummyPostJson($postId)]);

        $postService = new PostService();
        $postService->deletePost($username, $postId, $producer);

        $this->assertTrue(true);
    }

    public function test_delete_post_should_throw_resource_not_found_exception()
    {

        $producer = Mockery::mock(PostProducer::class);
        $producer->shouldReceive('produce')->never();

        Log::shouldReceive('info')->twice();

        $username = 'alimzhan';
        $postId = '8032a848-f765-4e63-8fbb-11acde9fefc7';
        $postKey = $username . ':' . $postId;

        Redis::shouldReceive('del')
            ->never();

        Redis::shouldReceive('lRange')
            ->with($postKey, 0, -1)
            ->once();

        $postService = new PostService();

        $this->expectException(ResourceNotFoundException::class);
        $this->expectExceptionCode(400);
        $this->expectExceptionMessage("Post with id $postId not found");

        $postService->deletePost($username, $postId, $producer);

    }

    public function test_delete_post_should_throw_not_allowed_exception()
    {

        $producer = Mockery::mock(PostProducer::class);
        $producer->shouldReceive('produce')->once();

        Log::shouldReceive('info')->twice();

        $username = 'alimzhan';
        $postId = '8032a848-f765-4e63-8fbb-11acde9fefc7';
        $postKey = $username . ':' . $postId;

        Redis::shouldReceive('del')
            ->with($postKey)
            ->once();

        Redis::shouldReceive('lRange')
            ->with($postKey, 0, -1)
            ->once()
            ->andReturn([$this->getDummyPostJson($postId, 'bruce')]);

        $postService = new PostService();

        $this->expectException(NotAllowedException::class);
        $this->expectExceptionCode(403);
        $this->expectExceptionMessage("user $username is not allowed to delete post id $postId");

        $postService->deletePost($username, $postId, $producer);
    }

    private function getDummyPostJson($id, $username = 'alimzhan') : string
    {
        return json_encode([
            'id' => $id,
            'imageUrl' => 'https://example.com/image.jpg',
            'caption' => 'Test caption',
            'lastModifiedBy' => $username,
            'username' => $username,
            'createdAt' => now(),
            'updatedAt' => now(),
        ]);
    }
}
