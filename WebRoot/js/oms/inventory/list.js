
$(document).ready(function() {
	document.title = '库存统计   | '+document.title;

    $('#menu_inventory').addClass('active').find('ul').addClass('in');
    var showItem = 'N';
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
    var dataTable = eeda.dt({
        "id": "eeda-table",
        "scrollY":"500px",
        "ajax": "/inventory/list",
        "columns": [
            { "data": "CARGO_NAME","class":"cargo_name"},
            { "data": "CARGO_BARCODE"},
            { "data": "CARGO_CODE"},
            { "data": "UNIT"},
            { "data": "CUSTOMER_NAME"},
            { "data": "SHELVES","width":"100px",
            	"render": function ( data, type, full, meta ) {
            		if(!data)
                        data='';
            		var value = '<input type="text" name="cargo_name" value="'+data+'" class="form-control shelves" />';
            		if(showItem=='N')
            			value = data;
                    return value;
            	}
            },
            { "data": "SHELF_LIFE"}, 
            { "data": "GATE_IN_AMOUNT"}, 
            { "data": "DAMAGE_AMOUNT"},
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
            		var damage_amount = data.DAMAGE_AMOUNT;
            		var amount = gate_in_amount - gate_out_amount- gate_out_amount-damage_amount;
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
    
    var showItem = 'N';
    $('#itemBtn').on('click',function(){
        if(showItem=='N'){
        	showItem = 'Y';
        	$(this).text('显示统计');
        }else{
        	showItem = 'N';
        	$(this).text('显示明细');
        }
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
    	var url = "/inventory/list?jsonStr="+JSON.stringify(itemJson)+"&showItem="+showItem;
        dataTable.ajax.url(url).load();
    };
    
   
    //动态更改单品库位
    $('#eeda-table').on('keyup','.shelves',function(e){    	
    	var shelves = $(this).val();
    	var order_id = $(this).parent().parent().attr("id");
    	var cargo_name = $(this).parent().parent().find(".cargo_name").text();

	    if(order_id=='' || shelves=='')
	    	return false;
    	
    	var key = e.which;
    	if (key == 13) {
    		$.post('/inventory/updateShelves',{order_id:order_id,shelves:shelves},function(data){
    			if(data){
    				$.scojs_message(cargo_name +' 更新库位为：'+shelves+' 成功!', $.scojs_message.TYPE_OK);
    			}else{
    				$.scojs_message('更新失败', $.scojs_message.TYPE_ERROR);
    			}
    		});
        }
    })
    
    
    
    
});