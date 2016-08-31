
$(document).ready(function() {
	document.title = '订单查询   | '+document.title;
    
    eeda.openMenu();
    
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
            { "data": "CONSIGNEE"}, 
            { "data": "GOODS_VALUE",'visible':false}, 
            { "data": "STATUS",
            	"render": function ( data, type, full, meta ) {
            		var msg = '';
            		if(data == '已通关'){
            			msg = "<span style='color:green'>"+data+"</span>";
            		}else if(data=='报关处理中'){
            			msg = "<span style='color:red'>"+data+"</span>";
            		}else{
            			msg = data;
            		}
            		return msg;
                }
            }, 
            { "data": "PAY_STATUS",
            	"render": function ( data, type, full, meta ) {
            		var msg = '';
            		if(data == null){
            			if(full.PAY_NO != null){
            				msg = '<span style="color:blue">已支付，未申报</span>';
            			}else{
            				msg = '未付款';
            			}
            		}else if(data=='接收成功'){
            			msg = "<span style='color:green'>付款已完成</span>";
            		}else{
            			msg = "<span style='color:red'>"+data+"</span>";
            		}
            		return msg;
                }
            }, 
            {"data": "LOG_STATUS",
            	"render": function ( data, type, full, meta ) {
	        		var msg = '';
	        		if(data==null){
	        			msg = '运单不存在';
	        		}else if(data=='已通关'){
	        			msg = "<span style='color:green'>"+data+"</span>";
	        		}else if(data=='报关处理中'){
	        			msg = "<span style='color:red'>"+data+"</span>";
	        		}else{
	        			msg = data;
	        		}
	        		return msg;
                }
            }, 
            {"data": "BILL_CUS_STATUS",
            	"render": function ( data, type, full, meta ) {
	        		var msg = '';
	        		if(data==null){
	        			msg = '清单不存在';
	        		}else if(data=='审批通过'){
	        			msg = "<span style='color:green'>"+data+"</span>";
	        		}else {
	        			msg = "<span style='color:red'>"+data+"</span>";
	        		}
	        		return msg;
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