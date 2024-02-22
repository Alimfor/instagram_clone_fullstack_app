<?php

use App\Http\Controllers\api\PostController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/


Route::group(['middleware' => ['jwt.verify']], function () {

    Route::post('posts/create', [PostController::class, 'store']);
    Route::delete('posts/{postId}', [PostController::class, 'destroy']);
    Route::get('posts/me', [PostController::class, 'findCurrentUserPosts']);
    Route::get('posts/{username}', [PostController::class, 'findPostsByUsername']);
    Route::post('posts/in', [PostController::class, 'findPostsByIdIn']);
});


