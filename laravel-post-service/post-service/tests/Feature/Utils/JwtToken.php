<?php

namespace Tests\Feature\Utils;

class JwtToken
{

    public static function getJwtToken() : string
    {
        return 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIGRldGFpbHMiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwidXNlcm5hbWUiOiJhbGltemhhbiIsImlzUmVmcmVzaFRva2VuIjpmYWxzZSwiaWF0IjoxNzA4NjA4ODYyLCJpc3MiOiJhdXRoLXNlcnZpY2UiLCJleHAiOjE3MDg2OTUyNjJ9.KVu-O5Fa71SRINR5T6G4pCqDSkqBM11EnI4Qas1_E04';
    }

    public static function getJwtToken2() : string
    {
        return 'here should be a jwt token that belongs to another user';
    }
}
