<?php
    if(!isset($_GET["location"])){
        echo "error: invalid input";
        exit(0);
    }
    
    $count = 0;
    $location = $_GET["location"];
    
    $con = mysqli_connect("localhost","freeprog_modify","cy@BAC2069","freeprog_mainDB");
    
    if($con==NULL){
        echo "error: failed to connect";
        exit(0);
    } else{
        $location = mysqli_real_escape_string($con,$location);
        
        $sql = "SELECT location FROM Last_locations;";
        
        $result = $con->query($sql);
        
        if ($result->num_rows > 0){
            list($my_latitude, $my_longitude) = explode("-", $location);
            if (is_numeric($my_latitude) && is_numeric($my_longitude)){
                $my_numeric_latitude = floatval($my_latitude);
                $my_numeric_longitude = floatval($my_longitude);
                while ($row = $result->fetch_assoc()){
                    list($latitude, $longitude) = explode(":", $row["location"]);
                    if (is_numeric($latitude) && is_numeric($longitude)){
                        $numeric_latitude = floatval($latitude);
                        $numeric_longitude = floatval($longitude);
                        $R = 6378.137;
                        $dLat = abs($numeric_latitude * pi() / 180 - $my_numeric_latitude * pi() / 180);
                        $dLat = abs($numeric_latitude * pi() / 180 - $numeric_latitude * pi() / 180);
                        $dLon = abs($numeric_longitude * pi() / 180 - $my_numeric_longitude * pi() / 180);
                        $a = sin($dLat/2) * sin($dLat/2) + cos($my_numeric_latitude * pi() / 180) * cos($numeric_latitude * pi() / 180) * sin($dLon/2) * sin($dLon/2);
                        $c = 2 * atan2(sqrt($a), sqrt(1-$a));
                        $d = $R * $c * 1000;
                        if ($d < 80)
                            $count = $count + 1;
                    } else {
                        echo "error: location not numeric";
                    }
                }
            } else {
                echo "error: my location not numeric";
            }
        } else {
        echo "error";
        exit(0);
        }
    }
    echo $count;
?>