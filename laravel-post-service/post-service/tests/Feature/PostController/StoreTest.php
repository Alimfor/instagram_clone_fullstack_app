<?php

namespace Tests\Feature\PostController;

use App\Services\PostService;
use Mockery;
use Tests\Feature\Utils\JwtToken;
use Tests\TestCase;

class StoreTest extends TestCase
{

    public static function numericDataProvider(): array
    {
        return [
            'Numeric image url' => [1, 'Test caption'],
            'Numeric caption' => ['https://example.com/image.jpg', 1],
            'Numeric data' => [1, 1],
        ];
    }

    public static function blankDataProvider(): array
    {
        return [
            'Blank image url' => ['', 'Test caption'],
            'Blank caption' => ['https://example.com/image.jpg', ''],
            'Blank data' => ['', ''],
        ];
    }

    public static function imageDataProvider(): array
    {
        return [
            'Non image url' => ["non image", 'Test caption'],
            'Image caption' => ["https://example.com/image.jpg", "https://example.com/image.jpg"],
        ];
    }


    public function test_store_should_return_201_status_after_saving(): void
    {
        $service = Mockery::mock(PostService::class);
        $this->app->instance(PostService::class, $service);
        $service->shouldReceive('savePost')->once()->andReturnTrue();

        $response = $this->withHeaders([
            'Authorization' => 'Bearer ' . JwtToken::getJwtToken()
        ])->postJson('/api/posts/create', [
            'imageUrl' => 'https://example.com/image.jpg',
            'caption' => 'Test caption',
        ]);

        $response->assertStatus(201);
    }

    /**
     * @dataProvider blankDataProvider
     */
    public function test_store_should_return_400_status_if_data_are_blank($imageUrl, $caption)
    {

        $this->withoutUsingPostService();
        $this->validateRequestInsideStoreMethod($imageUrl, $caption);
    }

    /**
     * @dataProvider numericDataProvider
     */
    public function test_store_should_return_400_status_if_data_are_numeric($imageUrl, $caption)
    {

        $this->withoutUsingPostService();
        $this->validateRequestInsideStoreMethod($imageUrl, $caption);
    }

    /**
     * @dataProvider imageDataProvider
     */
    public function test_store_should_return_400_status_after_if_data_response_to_another_request(
        $imageUrl, $caption
    )
    {

        $this->withoutUsingPostService();
        $this->validateRequestInsideStoreMethod($imageUrl, $caption);
    }

    private function withoutUsingPostService(): void
    {

        $service = Mockery::mock(PostService::class);
        $this->app->instance(PostService::class, $service);
        $service->shouldReceive('savePost')->never();
    }

    private function validateRequestInsideStoreMethod($imageUrl, $caption): void
    {

        $response = $this->withHeaders([
            'Authorization' => 'Bearer ' . JwtToken::getJwtToken()
        ])->postJson('/api/posts/create', [
            'imageUrl' => $imageUrl,
            'caption' => $caption,
        ]);

        $response->assertStatus(400);
    }
}
