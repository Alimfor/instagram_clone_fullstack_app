<?php

namespace Tests\Feature\Middleware;

use Tests\Feature\Utils\JwtToken;
use Tests\TestCase;

class AuthMiddlewareTest extends TestCase
{

    public function test_handle_valid_jwt_token(): void
    {

        $response = $this->withHeaders([
            'Authorization' => 'Bearer ' . JwtToken::getJwtToken()
        ])->postJson('/api/posts/in', [
            "temp uuid"
        ]);

        $response->assertStatus(400)
            ->assertJson(
                ['error' => 'Ids must be valid uuids']
            );
    }

    public function test_handle_invalid_jwt_token(): void
    {

        $invalidToken = 'invalid token';
        $response = $this->withHeaders([
            'Authorization' => 'Bearer ' . $invalidToken
        ])->postJson('/api/posts/in', [
            "temp uuid"
        ]);

        $response->assertStatus(401)
            ->assertJson(
                ['message' => 'This token is invalid. Please Login']
            );
    }

    public function test_handle_blank_jwt_token(): void
    {

        $blankToken = ' ';
        $response = $this->withHeaders([
            'Authorization' => 'Bearer ' . $blankToken
        ])->postJson('/api/posts/in', [
            "temp uuid"
        ]);

        $response->assertStatus(401)
            ->assertJson(
                [
                    'status' => 103,
                    'message' => 'Authorization Token not found'
                ]
            );
    }

}
