<?php

namespace App\Services;

use App\DTO\PostEventPayloadDTO;
use App\Exceptions\NotAllowedException;
use App\Exceptions\ResourceNotFoundException;
use App\Kafka\PostEventType;
use App\Kafka\PostProducer;
use App\Models\Post;
use Illuminate\Support\Carbon;
use Illuminate\Support\Collection;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Redis;
use Ramsey\Uuid\Uuid;

class PostService
{

    public function savePost(string $username, string $imageUrl, string $caption, PostProducer $producer): bool
    {
        $lastModifiedBy = $username;

        $uuid = Uuid::uuid4()->toString();
        $postKey = $username . ':' . $uuid;

        $post = new Post(
            $uuid,
            $imageUrl,
            $caption,
            $lastModifiedBy,
            $username,
            now(),
            now()
        );

        Redis::lPush($postKey, $post->toJson());
        $message = $this->convertTo($post, PostEventType::CREATED);
        $producer->produce($message);

        Log::info('Storing post is successful');
        return true;
    }


    public function deletePost($username, $postId, PostProducer $producer): void
    {
        $postKey = $username . ':' . $postId;

        $postArray = Redis::lrange($postKey, 0, -1);

        Log::info('postArray is null' . empty($postArray));

        if (empty($postArray)) {
            throw ResourceNotFoundException::notFound($postId);
        }

        $decodedPost = json_decode($postArray[0], true);

        $post = new Post(
            $decodedPost['id'],
            $decodedPost['imageUrl'],
            $decodedPost['caption'],
            $decodedPost['lastModifiedBy'],
            $decodedPost['username'],
            Carbon::parse($decodedPost['createdAt']),
            Carbon::parse($decodedPost['updatedAt'])
        );

        if ($post->getUsername() !== $username) {
            throw NotAllowedException::notAllowed($username, "post id $postId", "delete");
        }

        Redis::del($postKey);
        $message = $this->convertTo($post, PostEventType::DELETED);
        $producer->produce($message);

        Log::info('Deleting post is successful');
    }



    public function findPostsByUsername($username) : Collection
    {
        $postKeys = Redis::keys("$username:*");

        Log::info('postKeys size: ' . count($postKeys));

        $posts = collect($postKeys)->flatMap(function ($postKey) {
            $position = strpos($postKey, ':');
            $result = substr($postKey, $position + 1);
            return Redis::lrange($result, 0, -1);
        })->map(function ($post) {
            return json_decode($post, true);
        })->map(function ($decodedPost) {
            return new Post(
                $decodedPost['id'],
                $decodedPost['imageUrl'],
                $decodedPost['caption'],
                $decodedPost['lastModifiedBy'],
                $decodedPost['username'],
                Carbon::parse($decodedPost['createdAt']),
                Carbon::parse($decodedPost['updatedAt'])
            );
        });

        return $posts;
    }


    public function findPostsByIdIn($ids) : Collection
    {
        Log::info('ids count: ' . count($ids));

        $posts = collect([]);

        foreach ($ids as $userId) {
            $postKeys = Redis::keys("*:$userId");

            foreach ($postKeys as $postKey) {
                $result = explode(':', $postKey, 2)[1];
                $posts = $posts->merge(Redis::lrange($result, 0, -1));
            }
        }

        return $posts->map(function ($post) {
            $decodedPost = json_decode($post, true);

            return new Post(
                $decodedPost['id'],
                $decodedPost['imageUrl'],
                $decodedPost['caption'],
                $decodedPost['lastModifiedBy'],
                $decodedPost['username'],
                Carbon::parse($decodedPost['createdAt']),
                Carbon::parse($decodedPost['updatedAt'])
            );
        });
    }


    private function convertTo(Post $post, PostEventType $type): PostEventPayloadDTO
    {
        return new PostEventPayloadDTO(
            $post->getId(),
            $post->getUsername(),
            $post->getImageUrl(),
            $post->getCaption(),
            $type,
            $post->getLastModifiedBy(),
            $post->getCreatedAt(),
            $post->getUpdatedAt()
        );
    }
}
