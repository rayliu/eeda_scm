
$(document).ready(function() {
	document.title = '出库单查询   | '+document.title;

    $('#menu_incoming').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
	  //datatable, 动态处理
    var dataTable = eeda.dt({
        "id": "eeda-table",
        "ajax":"/gateOutOrder/list",
        "columns": [
            { "data": "ORDER_NO", 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/gateOutOrder/edit?id="+full.ID+"' >"+data+"</a>";
                }
            },
            { "data": "WAREHOUSE_NAME"},
            { "data": "CARGO_BARCODE"}, 
            { "data": "CUSTOMER_NAME"}, 
            { "data": "EXPRESS_NO"}, 
            { "data": "ORDER_TYPE"},
            { "data": "CONSIGNEE"}, 
            { "data": "CREATOR_NAME"}, 
            { "data": "CREATE_STAMP"}, 
            { "data": "STATUS"}
        ]
    });

    
    $('#resetBtn').click(function(e){
        $("#orderForm")[0].reset();
    });

    $('#searchBtn').click(function(){
        searchData(); 
    })

    buildCondition=function(){
    	var item = {};
    	var orderForm = $('#orderForm input,select');
    	for(var i = 0; i < orderForm.length; i++){
    		var name = orderForm[i].id;
        	var value =orderForm[i].value;
        	if(name){
        		item[name] = value;
        	}
    	}
        return item;
    };

    var searchData=function(){
    	var itemJson = buildCondition();
    	var url = "/gateOutOrder/list?jsonStr="+JSON.stringify(itemJson);
        dataTable.ajax.url(url).load();
    };

    
});