<?php

namespace App\DTO;

use App\Kafka\PostEventType;
use Illuminate\Contracts\Support\Jsonable;
use Illuminate\Support\Carbon;

class PostEventPayloadDTO
{
    public string $id;
    public string $username;
    public string $imageUrl;
    public string $caption;
    public PostEventType $eventType;
    public string $lastModifiedBy;
    public Carbon $createdAt;
    public Carbon $updatedAt;

    /**
     * @param string $id
     * @param string $username
     * @param string $imageUrl
     * @param string $caption
     * @param PostEventType $eventType
     * @param string $lastModifiedBy
     * @param Carbon $createdAt
     * @param Carbon $updatedAt
     */
    public function __construct(
        string $id, string $username,
        string $imageUrl, string $caption,
        PostEventType $eventType, string $lastModifiedBy,
        Carbon $createdAt, Carbon $updatedAt
    )
    {
        $this->id = $id;
        $this->username = $username;
        $this->imageUrl = $imageUrl;
        $this->caption = $caption;
        $this->eventType = $eventType;
        $this->lastModifiedBy = $lastModifiedBy;
        $this->createdAt = $createdAt;
        $this->updatedAt = $updatedAt;
    }

    public function __toString(): string
    {
        return json_encode($this);
    }


    public function toArray() {
        return [
            'id' => $this->id,
            'username' => $this->username,
            'imageUrl' => $this->imageUrl,
            'caption' => $this->caption,
            'eventType' => $this->eventType->value,
            'lastModifiedBy' => $this->lastModifiedBy,
            'createdAt' => $this->createdAt,
            'updatedAt' => $this->updatedAt
        ];
    }

    public function toJson($options = 0): bool|string
    {
        return json_encode([
            'id' => $this->id,
            'username' => $this->username,
            'imageUrl' => $this->imageUrl,
            'caption' => $this->caption,
            'eventType' => $this->eventType->value,
            'lastModifiedBy' => $this->lastModifiedBy,
            'createdAt' => $this->createdAt->toJSON(),
            'updatedAt' => $this->updatedAt->toJSON(),
        ]);
    }

}
