<?php

namespace App\Providers;

use Illuminate\Support\Facades\Validator;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register(): void
    {
        //
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        Validator::extend('contains_image_url', function ($attribute, $value, $parameters, $validator) {
            return preg_match('/\.(jpeg|png|jpg|svg)$/', $value) === 1;
        });

        Validator::replacer('contains_image_url', function ($message, $attribute, $rule, $parameters) {
            return str_replace(':attribute', $attribute, 'The :attribute must contain a valid image URL.');
        });

        Validator::extend('not_image_url', function ($attribute, $value, $parameters, $validator) {
            if (filter_var($value, FILTER_VALIDATE_URL) &&
                preg_match('/\.(jpeg|png|jpg|svg)$/', $value)
            ) {
                return false;
            }
            return true;
        });

        Validator::replacer('not_image_url', function ($message, $attribute, $rule, $parameters) {
            return str_replace(':attribute', $attribute, 'The :attribute must not be an image URL.');
        });
    }
}
