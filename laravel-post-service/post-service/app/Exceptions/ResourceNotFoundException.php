<?php

namespace App\Exceptions;

use Exception;

class ResourceNotFoundException extends CustomException
{
    public static function notFound(string $postId) : self
    {
        return new self("Post with id {$postId} not found", 400);
    }
}
