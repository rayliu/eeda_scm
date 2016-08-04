
$(document).ready(function() {
	document.title = '库存统计   | '+document.title;

    $('#menu_inventory').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
    var dataTable = eeda.dt({
        "id": "eeda-table",
        "ajax": "/inventory/list",
        "columns": [
            { "data": "CARGO_NAME"/*, 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/gateInOrder/edit?id="+full.ID+"''>"+data+"</a>";
                }*/
            },
            { "data": "CARGO_CODE"},
            { "data": "UNIT"},
            { "data": "CUSTOMER_NAME"},
            { "data": "SHELVES"}, 
            { "data": "SHELF_LIFE"}, 
            { "data": "GATE_IN_AMOUNT"}, 
            { "data": "LOCK_AMOUNT",
            	"render": function ( data, type, full, meta ) {
            		if(data>0)
            			return "<span style='color:green;font-weight:bold;'>"+data+"</span>";
            		else
            			return data;
            	}
            }, 
            { "data": "GATE_OUT_AMOUNT"},
            { "data": null,
            	"render": function ( data, type, full, meta ) {
            		var gate_out_amount = data.GATE_OUT_AMOUNT;
            		var gate_in_amount = data.GATE_IN_AMOUNT;
            		var amount = gate_in_amount - gate_out_amount;
            		if(amount>0)
            			return "<span style='color:red;font-weight:bold;'>"+amount+"</span>";
            		else
            			return amount;
                }
            }
        ]
    });
	  //datatable, 动态处理
    var dataTable = $('#eeda-table').DataTable();

    
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
    	var url = "/inventory/list?jsonStr="+JSON.stringify(itemJson);
        dataTable.ajax.url(url).load();
    };
    
});