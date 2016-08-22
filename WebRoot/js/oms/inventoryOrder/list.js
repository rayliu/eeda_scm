
$(document).ready(function() {
	document.title = '库存统计   | '+document.title;

    $('#menu_inventory').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
    var dataTable = eeda.dt({
        "id": "eeda-table",
        "ajax": "/inventoryOrder/list",
        "columns": [
            { "data": "ORDER_NO", 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/inventoryOrder/edit?id="+full.ID+"''>"+data+"</a>";
                }
            },
            { "data": "WAREHOUSE_NAME"},
            { "data": "CHECK_STAMP"},
            { "data": "AUDIT_STAMP"},
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
    	var url = "/inventoryOrder/list?jsonStr="+JSON.stringify(itemJson);
        dataTable.ajax.url(url).load();
    };
    
});