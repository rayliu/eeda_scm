
$(document).ready(function() {
	document.title = '移库单查询   | '+document.title;

    $('#menu_incoming').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
	  //datatable, 动态处理
    var dataTable = eeda.dt({
        "id": "eeda-table",
        "ajax": "/customInventory/list",
        "columns": [
            { "data": "ITEM_NAME" },
            { "data": "ITEM_NO"}, 
            { "data": "SERIAL_NO"}, 
            { "data": "AMOUNT"},
            { "data": "PUSH_AMOUNT"},
            { "data": "NOPUSH_AMOUNT"}
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
    	var url = "/customInventory/list?jsonStr="+JSON.stringify(itemJson);
        dataTable.ajax.url(url).load();
    };
    
});