<?php

namespace App\Http\Controllers\api;

use App\Exceptions\NotAllowedException;
use App\Exceptions\ResourceNotFoundException;
use App\Http\Controllers\Controller;
use App\Http\Requests\PostRequest;
use App\Kafka\PostProducer;
use App\Services\PostService;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Illuminate\Support\Collection;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Validator;
use Tymon\JWTAuth\Facades\JWTAuth;


class PostController extends Controller
{

    public function __construct(
        protected PostService $postService)
    {

    }

    public function store(PostRequest $request, PostProducer $producer): JsonResponse
    {

        $validator = Validator::make($request->all(), [
            'imageUrl' => 'required|string|min:5|contains_image_url',
            'caption' => 'required|string|min:5|not_image_url',
        ]);

        if ($validator->fails()) {
            $errors = $validator->errors();
            return response()->json(['errors' => $errors], 400);
        }

        $username = $this->getAuthenticatedUsername();

        Log::info('Storing post for user: ' . $username);

        $this->postService->savePost(
            $username, $request->imageUrl,
            $request->caption, $producer
        );

        return response()->json(['status' => 'success'], 201);
    }

    public function destroy(Request $request, $postId, PostProducer $producer): Response|JsonResponse
    {

        $validator = Validator::make(['postId' => $postId], [
            'postId' => 'uuid'
        ]);

        if ($validator->fails()) {
            $errors = 'Id must be valid uuids';
            return response()->json(['error' => $errors], 400);
        }

        $username = $this->getAuthenticatedUsername();

        Log::info('Deleting post for user: ' . $username);
        try {
            $this->postService->deletePost($username, $request->postId, $producer);
        } catch (NotAllowedException|ResourceNotFoundException $e) {
            return response()->json(['error' => $e->getMessage()], $e->getCode());
        }

        return response()->noContent();
    }

    public function findCurrentUserPosts(): JsonResponse
    {
        $username = $this->getAuthenticatedUsername();

        Log::info('Fetching posts by username for user: ' . $username);
        $posts = $this->postService->findPostsByUsername($username);

        Log::info('Fetched posts size is ' . count($posts));
        return response()->json($posts);
    }

    public function findPostsByUsername(Request $request, $username): JsonResponse
    {

        $validator = Validator::make(['username' => $username], [
            'username' => ['required', 'string', 'min:3', 'max:15'],
        ]);

        if ($validator->fails()) {
            return response()->json(['errors' => $validator->errors()], 400);
        }

        Log::info('Fetching ' . $request->username . ' posts');
        $posts = $this->postService->findPostsByUsername($request->username);

        Log::info('Fetched posts size is ' . count($posts));
        return response()->json($posts);
    }

    public function findPostsByIdIn(Request $request): JsonResponse
    {

        $validator = Validator::make($request->all(), [
            '*' => 'uuid'
        ]);

        if ($validator->fails()) {
            $errors = 'Ids must be valid uuids';
            return response()->json(['error' => $errors], 400);
        }

        $ids = $request->json()->all();
        Log::info('Fetching posts for ' . count($ids) . ' ids');

        $posts = $this->postService->findPostsByIdIn($ids);

        Log::info('Fetched posts size is ' . count($posts));
        return response()->json($posts);
    }

    private function getAuthenticatedUsername(): string
    {
        $payload = JWTAuth::parseToken()->getPayload();
        return $payload->get('username');
    }
}
