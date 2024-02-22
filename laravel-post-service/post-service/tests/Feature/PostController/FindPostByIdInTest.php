<?php

namespace Tests\Feature\PostController;

use App\Services\PostService;
use Illuminate\Testing\TestResponse;
use Mockery;
use Tests\Feature\Utils\JwtToken;
use Tests\TestCase;

class FindPostByIdInTest extends TestCase
{
    public static function idsProvider() : array
    {
        return [
            'Multiple identifiers' => [
                    "8032a848-f765-4e63-8fbb-11acde9fefc7",
                    "71f9460b-cbca-4238-84f7-301c5b192f0b"
            ],
            'Single identifier' => ["8032a848-f765-4e63-8fbb-11acde9fefc7"]
        ];
    }

    public static function nonValidIdsProvider() : array
    {
        return [
            'Numeric id' => [1],
            'Id with a few characters' => ['alfsd'],
            'ids with different types' => ['alfsd', 1]
        ];
    }


    /**
     * @dataProvider idsProvider
     */
    public function test_find_posts_by_id_in_should_return_200_status($ids) : void
    {

        $service = Mockery::mock(PostService::class);
        $this->app->instance(PostService::class, $service);

        $service->shouldReceive('findPostsByIdIn')->once()->andReturn(collect());

        $response = $this->callEndpointAndReturnResponse(JwtToken::getJwtToken(), $ids);
        $response->assertStatus(200);
    }

    /**
     * @dataProvider nonValidIdsProvider
     */
    public function test_find_posts_by_id_in_should_return_400_status_if_ids_are_invalid($ids)
    {

        $service = Mockery::mock(PostService::class);
        $this->app->instance(PostService::class, $service);

        $service->shouldReceive('findPostsByIdIn')->never();

        $response = $this->callEndpointAndReturnResponse(JwtToken::getJwtToken(), $ids);
        $response->assertStatus(400)
        ->assertJson([
            'error' => 'Ids must be valid uuids'
        ]);
    }

    /**
     * @dataProvider idsProvider
     */
    public function test_find_posts_by_id_in_should_return_specific_json_structure($ids)
    {

        $service = Mockery::mock(PostService::class);
        $this->app->instance(PostService::class, $service);

        $service->shouldReceive('findPostsByIdIn')->once()->andReturn(collect());

        $response = $this->callEndpointAndReturnResponse(JwtToken::getJwtToken(), $ids);
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

    private function callEndpointAndReturnResponse($token, $ids) :TestResponse
    {
        return $this->withHeaders([
            'Authorization' => 'Bearer ' . $token
        ])->postJson('/api/posts/in', [$ids]);
    }
}
