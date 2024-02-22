<?php

namespace App\Models;

use Illuminate\Contracts\Support\Jsonable;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Carbon;

class Post implements Jsonable
{
    private string $id;
    private string $username;
    private string $imageUrl;
    private string $caption;
    private string $lastModifiedBy;
    private Carbon $createdAt;
    private Carbon $updatedAt;

    /**
     * @param string $id
     * @param string $username
     * @param string $imageUrl
     * @param string $caption
     * @param string $lastModifiedBy
     * @param Carbon $createdAt
     * @param Carbon $updatedAt
     */
    public function __construct(
        string $id, string $imageUrl, string $caption, string $lastModifiedBy,
        string $username, Carbon $createdAt, Carbon $updatedAt
    )
    {
        $this->id = $id;
        $this->username = $username;
        $this->imageUrl = $imageUrl;
        $this->caption = $caption;
        $this->lastModifiedBy = $lastModifiedBy;
        $this->createdAt = $createdAt;
        $this->updatedAt = $updatedAt;
    }


    public function getId(): string
    {
        return $this->id;
    }

    public function getUsername(): string
    {
        return $this->username;
    }

    public function getImageUrl(): string
    {
        return $this->imageUrl;
    }

    public function getCaption(): string
    {
        return $this->caption;
    }

    public function getLastModifiedBy(): string
    {
        return $this->lastModifiedBy;
    }

    public function getCreatedAt(): Carbon
    {
        return $this->createdAt;
    }

    public function getUpdatedAt(): Carbon
    {
        return $this->updatedAt;
    }

    public function toJson($options = 0)
    {
        return json_encode($this->toArray(), $options);
    }

    public function toArray()
    {
        return [
            'id' => $this->id,
            'username' => $this->username,
            'imageUrl' => $this->imageUrl,
            'caption' => $this->caption,
            'lastModifiedBy' => $this->lastModifiedBy,
            'createdAt' => $this->createdAt->toJson(),
            'updatedAt' => $this->updatedAt->toJson(),
        ];
    }
}
