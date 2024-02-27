<?php

namespace Tests\Feature\Utils;

class JwtToken
{

    public static function getJwtToken() : string
    {
        return 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIGRldGFpbHMiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwidXNlcm5hbWUiOiJhbGV4IiwiaXNSZWZyZXNoVG9rZW4iOmZhbHNlLCJpYXQiOjE3MDkwMDU3NjMsImlzcyI6ImF1dGgtc2VydmljZSIsImV4cCI6MTcwOTA5MjE2M30.d6omifMZNQNN19FDESYy60k5wH8yP-bU7yfB6JEhhhM';
    }

    public static function getJwtToken2() : string
    {
        return 'here should be a jwt token that belongs to another user';
    }
}
