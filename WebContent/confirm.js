

function handleResultData(resultData){
    console.log(resultData);
    console.log("Point 1");
    let item_list = jQuery("#movies-purchased");
    let total_price = jQuery("#total-price");
    let saleId = jQuery("#sale-id");
    console.log("Point 2");

    console.log("length: " + resultData.length);
    // change it to html list
    let i = 0;
    while (i < resultData.length-1) {
        console.log("Point 3");

        console.log("entering loop: " + i);
        // each item will be in a bullet point
        let res = "";
        res += "<tr>";
        res += "<th>" + resultData[i]["movie_title"] + "</th>";
        res += "<th>" + resultData[i]["quantity"] + "</th>";
        res += "<th>" + resultData[i]["price"] + "</th>";
        res += "<tr>";
        item_list.append(res);
        i += 1;
    }
    // clear the old array and show the new array in the frontend
    console.log("Point 4");

    saleId.append(resultData[i]["sale_id"]);
    total_price.text(resultData[i]["total_price"]);

    console.log("Point 5");

}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/confirm",
    success: (resultData) => handleResultData(resultData),
    error: function(xhr, status, error) {
        console.error("Error: " + error);
    }
});