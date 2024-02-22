<?php

namespace App\Kafka;

use App\DTO\PostEventPayloadDTO;
use Illuminate\Support\Facades\Log;
use Junges\Kafka\Facades\Kafka;
use Junges\Kafka\Message\Message;
use RdKafka\Conf;
use RdKafka\Producer;

//use RdKafka\Conf;
//use RdKafka\Producer;

class PostProducer
{

    public function produce(PostEventPayloadDTO $message): void
    {

        $new_message = new Message(
            headers: ['kafka_receivedMessageKey' => $message->id],
            body: $message->toArray(),
            key: $message->id
        );

        $producer = Kafka::publishOn('post-changed-event')
            ->withMessage($new_message);
        try {
            $producer->send();
        } catch (\Exception $e) {
            Log::error("Failed to produce message: " . $e->getMessage());
        }
    }

}
