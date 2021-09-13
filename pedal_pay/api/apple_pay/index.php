<?php

/**
* Derived From https://stripe.com/docs/mobile/ios/standard
*/

require('./vendor/autoload.php');

$token = $_POST['stripeToken'];
$amount = $_POST['amount'];

try{
\Stripe\Stripe::setApiKey("");

\Stripe\Charge::create(array(
    "amount" => round($amount * 100),
    "currency" => "gbp",
    "source" => $token,
    "description" => "Pedal Pay Transaction",
));
} catch (Stripe_CardError $e) {
    $body = $e->getJsonBody();
    $err  = $body['error'];

    echo 'Status is:' . $e->getHttpStatus() . "\n";
    echo 'Type is:' . $err['type'] . "\n";
    echo 'Code is:' . $err['code'] . "\n";
    echo 'Param is:' . $err['param'] . "\n";
    echo 'Message is:' . $err['message'] . "\n";
} catch (Exception $e) {
    echo $e->getMessage();
} catch (ErrorException $e) {
    echo $e->getMessage();
}
