<?php

$target_dir = "uploads/";
$target_file = $target_dir . basename($_FILES["file"]["name"]);
$uploadOk = 1;
$imageFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));

if(isset($_FILES["file"])) {
    $check = getimagesize($_FILES["file"]["tmp_name"]);
    if($check !== false) {
        $uploadOk = 1;
    } else {
        echo "error: file is not an image";
        $uploadOk = 0;
    }
} else{
    echo "error: invalid input";
    exit(0);
}

if ($_FILES["fileToUpload"]["size"] > 500000) {
    echo "error: file too large";
    $uploadOk = 0;
}

if($imageFileType != "jpg" && $imageFileType != "png" && $imageFileType != "jpeg" && $imageFileType != "gif" ) {
    echo "error: only JPG, JPEG, PNG & GIF files";
    $uploadOk = 0;
}

if ($uploadOk == 0) {
    exit(0);
} else {
    if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file)) {
        echo "success";
    } else {
        echo "error: uploading file";
    }
}

?>
