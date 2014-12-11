<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 3.11.2014.
 * Time: 21:17
 */

class Crypter {

    private static $pass = "aksjdf654asddf";

    /**
     *
     * Encryption function. Too complicated to understand the whole process. Use encrypt and decrypt for secure data transfers.
     * @param $data
     * @param string $type
     * @return string
     */
    public static function encrypt($data, $type = "aes128") {
        $iv = Crypter::getIv(16);
        $encrypted = openssl_encrypt($data, $type, Crypter::$pass, false, $iv);
        $cryptedText = $iv . $encrypted;
        $guid = str_replace(array(chr(123), chr(125)), '', Crypter::createGuid());
        $callback = function($part, $run) use ($cryptedText){
            $splitted = str_split($cryptedText, 8);
            return $part . $splitted[$run];
        };

        $finalEncryption = Crypter::moreHide($guid, 0, $callback);

        return base64_encode($finalEncryption);

    }

    /**
     * Decryption function. Reverse process of encryption. Also too complicated to understand.
     *
     * @param $data
     * @return string
     */
    public static function decrypt($data) {
        $step1 = base64_decode($data);
        $step2 = substr($step1, 8, 8).substr($step1, 20, 8) . substr($step1, 32,8). substr($step1, 44, 8) . substr($step1,64);
        $iv = substr($step2,0,16);
        $step3 = substr($step2, 16);
        $text = openssl_decrypt($step3, "aes128", Crypter::$pass, false, $iv);
        return $text;
    }



    // Helper functions for encryption
    private static function getIv($num_bytes) {
        $rand = openssl_random_pseudo_bytes($num_bytes);
        return $rand;
    }

    private static function moreHide($data, $run, $callback){

        $parts = substr($data, 0, strpos($data, '-'));
        $rest = substr($data, strpos($data, '-') + 1, strlen($data));


        $hided = $callback($parts, $run);
        $LookAtMe = $hided . $rest;
        if(strpos($LookAtMe, '-') === false){
        return $callback($LookAtMe, $run + 1);
        }else{
            return Crypter::moreHide($LookAtMe, $run + 1, $callback);

        }

    }

    private static function createGuid(){
        if (function_exists('com_create_guid')){
            return com_create_guid();
        }else{
            mt_srand((double)microtime()*10000);//optional for php 4.2.0 and up.
            $charid = strtoupper(md5(uniqid(rand(), true)));
            $hyphen = chr(45);
            $uuid = chr(123)
                .substr($charid, 0, 8).$hyphen
                .substr($charid, 8, 4).$hyphen
                .substr($charid,12, 4).$hyphen
                .substr($charid,16, 4).$hyphen
                .substr($charid,20,12)
                .chr(125);
            return $uuid;
        }
    }
} 