<?php

namespace Tests\Feature\PostController;

use App\Services\PostService;
use Illuminate\Testing\TestResponse;
use Mockery;
use Tests\Feature\Utils\JwtToken;
use Tests\TestCase;

class FindCurrentUserPostsTest extends TestCase
{

    public function test_find_current_user_posts_should_return_200_status(): void
    {

        $this->withUsingPostService();
        $response = $this->callEndpointAndReturnResponse(JwtToken::getJwtToken());
        $response->assertStatus(200);
    }

    public function test_find_current_user_posts_should_return_specific_json_structure()
    {

        $this->withUsingPostService();
        $response = $this->callEndpointAndReturnResponse(JwtToken::getJwtToken());
        $response->assertJsonStructure([
            '*' => [
                'id',
                'username',
                'imageUrl',
                'caption',
                'lastModifiedBy',
                'createdAt',
                'updatedAt'
            ]
        ]);
    }

    private function withUsingPostService() : void
    {
        $service = Mockery::mock(PostService::class);
        $this->app->instance(PostService::class, $service);

        $service->shouldReceive('findPostsByUsername')->once()->andReturn(collect());
    }

    private function callEndpointAndReturnResponse($token) :TestResponse
    {
        return $this->withHeaders([
            'Authorization' => 'Bearer ' . $token
        ])->getJson('/api/posts/me');
    }
}
