<?php

namespace App\Exceptions;

use Exception;

class NotAllowedException extends CustomException
{
    public static function notAllowed($username, $resource, $operation) : self
    {
        return new self("user $username is not allowed to $operation $resource", 403);
    }
}
