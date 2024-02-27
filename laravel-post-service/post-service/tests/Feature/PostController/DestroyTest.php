<?php

namespace Tests\Feature\PostController;

use App\Services\PostService;
use Illuminate\Testing\TestResponse;
use Mockery;
use Tests\Feature\Utils\JwtToken;
use Tests\TestCase;

class DestroyTest extends TestCase
{

    public static function invalidIdProvider(): array
    {
        return [
            'Uuid with a few number' => ['123'],
            'Uuid with a few letter' => ['abc'],
            'Uuid with a few number and letter' => ['123abc'],
        ];
    }

    public function test_destroy_should_return_204_after_deleting()
    {

        $validPostId = "976bc69e-cb59-4e4d-b685-ebfe99c8a3d7";

        $service = Mockery::mock(PostService::class);
        $this->app->instance(PostService::class, $service);

        $service->shouldReceive('deletePost')->once();

        $response = $this->callEndpointAndReturnResponse(JwtToken::getJwtToken(), $validPostId);
        $response->assertStatus(204);
    }

    /**
     * @dataProvider invalidIdProvider
     */
    public function test_destroy_should_return_400_if_id_is_invalid($invalidPostId)
    {

        $service = Mockery::mock(PostService::class);
        $this->app->instance(PostService::class, $service);

        $service->shouldReceive('deletePost')->never();

        $response = $this->callEndpointAndReturnResponse(JwtToken::getJwtToken(), $invalidPostId);
        $response->assertStatus(400)
            ->assertJson([
                'error' => 'Id must be valid uuids'
            ]);
    }


    private function callEndpointAndReturnResponse($token, $postId): TestResponse
    {
        return $this->withHeaders([
            'Authorization' => 'Bearer ' . $token
        ])->deleteJson("/api/posts/$postId");
    }
}
