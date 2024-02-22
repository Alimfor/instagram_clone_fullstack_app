<?php

namespace App\Http\Middleware;

use Closure;
use Exception;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use Symfony\Component\HttpFoundation\Response;
use Tymon\JWTAuth\Exceptions\JWTException;
use Tymon\JWTAuth\Exceptions\TokenInvalidException;
use Tymon\JWTAuth\Facades\JWTAuth;
use Tymon\JWTAuth\JWT;

class AuthMiddleware
{
    /**
     * Handle an incoming request.
     *
     * @param \Closure(\Illuminate\Http\Request): (\Symfony\Component\HttpFoundation\Response) $next
     */
    public function handle(Request $request, Closure $next): Response
    {
        try {

            $payload = JWTAuth::parseToken()->getPayload();

            Log::info('user found: '. $payload->get('username'));
        } catch (TokenInvalidException $e) {

            $message = 'This token is invalid. Please Login';
            return response()->json(compact( 'message'), 401);
        } catch (JWTException $e) {

            $status = 103;
            $message = 'Authorization Token not found';
            return response()->json(compact( 'status','message'), 401);
        } catch (Exception $e) {

            $message = 'Authorization Token not found';
            return response()->json(compact($e->getMessage()), 404);
        }

        return $next($request);
    }
}
