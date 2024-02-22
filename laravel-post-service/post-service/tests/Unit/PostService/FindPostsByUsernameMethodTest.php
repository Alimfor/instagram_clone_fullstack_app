<?php

namespace Tests\Unit\PostService;

use App\Models\Post;
use App\Services\PostService;
use Illuminate\Support\Carbon;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Redis;
use PHPUnit\Framework\TestCase;

class FindPostsByUsernameMethodTest extends TestCase
{

    public function test_find_posts_by_username_should_return_posts()
    {
        $username = 'alimzhan';
        $post_keys = [
            '71f9460b-cbca-4238-84f7-301c5b192f0b',
            '06d9922c-2d5f-48c5-a83d-b9b609063ca6'
        ];

        $posts = [
            [
                'id' => '71f9460b-cbca-4238-84f7-301c5b192f0b',
                'imageUrl' => 'https://phppot.com/wp-content/uploads/2021/07/php-laravel-request-lifecycle.jpg',
                'caption' => 'example picture',
                'lastModifiedBy' => $username,
                'username' => $username,
                'createdAt' => '2024-02-20T07:52:21.935191Z',
                'updatedAt' => '2024-02-20T07:52:21.935197Z'
            ],
            [
                'id' => '06d9922c-2d5f-48c5-a83d-b9b609063ca6',
                'imageUrl' => 'https://phppot.com/wp-content/uploads/2021/07/php-laravel-request-lifecycle.jpg',
                'caption' => 'example picture',
                'lastModifiedBy' => $username,
                'username' => $username,
                'createdAt' => '2024-02-18T12:32:11.546948Z',
                'updatedAt' => '2024-02-18T12:32:11.546956Z'
            ]
        ];

        Redis::shouldReceive('keys')
            ->with("{$username}:*")
            ->once()
            ->andReturn([
                    "insta-clone:$username:$post_keys[0]",
                    "insta-clone:$username:$post_keys[1]"
                ]
            );

        Log::shouldReceive('info')->once();

        Redis::shouldReceive('lrange')
            ->with("$username:$post_keys[0]", 0, -1)
            ->once()
            ->andReturn([json_encode($posts[0])]);

        Redis::shouldReceive('lrange')
            ->with("$username:$post_keys[1]", 0, -1)
            ->once()
            ->andReturn([json_encode($posts[1])]);

        $postService = new PostService();
        $result = $postService->findPostsByUsername($username);

        $this->assertCount(2, $result);
        $this->assertEquals($posts[0]['id'], $result[0]->getId());
        $this->assertEquals($posts[1]['id'], $result[1]->getId());
    }
}
