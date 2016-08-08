
$(document).ready(function() {
	document.title = '订单查询   | '+document.title;

    $('#menu_order').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
	  //datatable, 动态处理
    var dataTable = eeda.dt({
        "id": "eeda-table",
        "ajax": "/salesOrder/list",
        "columns": [
            { "data": "ORDER_NO", 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/salesOrder/edit?id="+full.ID+"'>"+data+"</a>";
                }
            },
            { "data": "SHOP_NAME"},
            { "data": "PAYER_NAME"}, 
            { "data": "GOODS_VALUE"}, 
            { "data": null,
            	"render": function ( data, type, full, meta ) {
            		var order_cus_status = data.ORDER_CUS_STATUS;
            		var order_ciq_status = data.ORDER_CIQ_STATUS;
            		var pay_status = data.PAY_STATUS;
            		
            		if(order_cus_status=='接收成功' && order_ciq_status=='接收成功' && pay_status=='接收成功'){
            			return '<span style="color:green">订单报关已完成</span>';
            		}else{
            			return '<span style="color:red">订单报关处理中</span>';
            		}
            	}
            }, 
            { "data": "CREATOR_NAME"}, 
            { "data": "CREATE_STAMP"}
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
    	var url = "/salesOrder/list?jsonStr="+JSON.stringify(itemJson);
        dataTable.ajax.url(url).load();
    };
    
});