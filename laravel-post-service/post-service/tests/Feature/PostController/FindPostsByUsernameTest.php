<?php

namespace Tests\Feature\PostController;

use App\Services\PostService;
use Illuminate\Testing\TestResponse;
use Mockery;
use Tests\Feature\Utils\JwtToken;
use Tests\TestCase;

class FindPostsByUsernameTest extends TestCase
{

    public static function nonValidUsernameProvider() : array
    {
        return [
            'Numeric username' => [1],
            'Username with 2 characters' => ['al'],
            'Username with 16 characters' => ['alimzhanalimzhan'],
        ];
    }

    public function test_find_posts_by_username_should_return_200_status() : void
    {
        $username = 'alimzhan';
        $service = Mockery::mock(PostService::class);
        $this->app->instance(PostService::class, $service);

        $service->shouldReceive('findPostsByUsername')->once()->andReturn(collect());

        $response = $this->callEndpointAndReturnResponse(JwtToken::getJwtToken(), $username);

        $response->assertStatus(200);
    }

    /**
     * @dataProvider nonValidUsernameProvider
     */
    public function test_find_posts_by_username_should_return_400_status_if_username_is_invalid(
        $username
    )
    {
        $service = Mockery::mock(PostService::class);
        $this->app->instance(PostService::class, $service);
        $service->shouldReceive('findPostsByUsername')->never();
        $response = $this->callEndpointAndReturnResponse(JwtToken::getJwtToken(), $username);
        $response->assertStatus(400);
    }


    private function callEndpointAndReturnResponse($token, $username) :TestResponse
    {
        return $this->withHeaders([
            'Authorization' => 'Bearer ' . $token
        ])->getJson("/api/posts/$username");
    }
}
