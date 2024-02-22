<?php

namespace App\Kafka;

enum PostEventType : string
{

    case CREATED = 'CREATED';
    case UPDATED = 'UPDATED';
    case DELETED = 'DELETED';

}
