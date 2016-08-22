 
$(document).ready(function() {

    var deletedTableIds=[];
    var table_name = 'cargo_table';

    //删除一行
    $("#"+table_name).on('click', '.delete', function(e){
        e.preventDefault();
        var tr = $(this).parent().parent();
        deletedTableIds.push(tr.attr('id'))
        
        cargoTable.row(tr).remove().draw();
    }); 

    gateInOrder.buildCargoDetail=function(){
        var item_table_rows = $("#"+table_name+" tr");
        var items_array=[];
        for(var index=0; index<item_table_rows.length; index++){
            if(index==0)
                continue;

            var row = item_table_rows[index];
            var empty = $(row).find('.dataTables_empty').text();
            if(empty)
            	continue;
            
            var id = $(row).attr('id');
            if(!id){
                id='';
            }
            
            var item={}
            item.id = id;
            for(var i = 1; i < row.childNodes.length; i++){
            	var name = $(row.childNodes[i]).find('input,select').attr('name');
            	var value = $(row.childNodes[i]).find('input,select').val();
            	if(name){
            		item[name] = value;
            	}
            }
            item.action = id.length > 0?'UPDATE':'CREATE';
            items_array.push(item);
        }

        //add deleted items
        for(var index=0; index<deletedTableIds.length; index++){
            var id = deletedTableIds[index];
            var item={
                id: id,
                action: 'DELETE'
            };
            items_array.push(item);
        }
        deletedTableIds = [];
        return items_array;
    };
    
    var bindFieldEvent=function(){
    	$('table .date').datetimepicker({  
    	    format: 'yyyy-MM-dd',  
    	    language: 'zh-CN'
    	}).on('changeDate', function(el){
    	    $(".bootstrap-datetimepicker-widget").hide();   
    	    $(el).trigger('keyup');
    	});
    };

    //------------事件处理
    var cargoTable = $("#"+table_name).DataTable({
        "processing": true,
        "searching": false,
        "paging": false,
        "info": false,
        "autoWidth": false,
        "serverSide": false,
        "scrollX":  true,
        "responsive": true,
        "scrollY":  true, 
        "scrollCollapse":  true,
        "language": {
            "url": "/yh/js/plugins/datatables-1.10.9/i18n/Chinese.json"
        },
        "createdRow": function ( row, data, index ) {
            $(row).attr('id', data.ID);
        },"drawCallback": function( settings ) {
	        bindFieldEvent();
	    },
        "columns": [
            { "width": "30px",
                "render": function ( data, type, full, meta ) {
                	return '<button type="button" class="delete btn btn-default btn-xs">删除</button> ';
                }
            },
            { "width": "30px", 
                "render": function ( data, type, full, meta ) {
                    return meta.row+1;
                }
            },
            { "data": "CARGO_UPC", "width": "120px", 
            	"render": function ( data, type, full, meta ) {
            		if(!data)
            			data='';
            		return '<input type="text" name="cargo_upc" value="'+data+'" class="form-control" />';
            	}
            },
            { "data": "CARGO_NAME",  "width": "200px",
            	"render": function ( data, type, full, meta ) {
            		if(!data)
            			data='';
            		return '<input type="text" name="cargo_name" value="'+data+'" class="form-control" />';
            	}
            },
            { "data": "PLAN_AMOUNT", 
            	"render": function ( data, type, full, meta ) {
            		if(!data)
            			data='';
            		return '<input type="text" name="plan_amount" value="'+data+'" class="form-control" />';
            	}
            },
            { "data": "RECEIVED_AMOUNT",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="received_amount" value="'+data+'" class="form-control" disabled/>';
                }
            },
            { "data": "SHELF_LIFE", "width": "120px",
            	"render": function ( data, type, full, meta ) {
            		if(!data)
            			data='';
            		var field_html = template('table_date_field_template',
		                    {
		                        id: 'shelf_life',
		                        name:'shelf_life',
		                        value: data.substr(0,19)
		                    }
		                );
	                    return field_html;
            		//return '<input type="text" value="'+data+'" class="form-control" required/>';
            	}
            },
            { "data": "CARTON_NO", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="carton_no" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "PACKING_UNIT","width": "60px",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   var str= '<select class="form-control search-control" name="packing_unit">'
            	   	   +'<option></option>'
            	   	   +'<option value="罐" '+ (data=='罐'?'selected':'') +'>罐</option>'
	                   +'<option value="台" '+ (data=='台'?'selected':'') +'>台</option>'
	                   +'<option value="件" '+ (data=='件'?'selected':'') +'>件</option>'
	                   +'<option value="套" '+ (data=='套'?'selected':'') +'>套</option>'
	                   +'<option value="条" '+ (data=='条'?'selected':'') +'>条</option>'
	                   +'<option value="本" '+ (data=='本'?'selected':'') +'>本</option>'
	                   +'<option value="颗" '+ (data=='颗'?'selected':'') +'>颗</option>'
	                   +'<option value="棵" '+ (data=='棵'?'selected':'') +'>棵</option>'
	                   +'<option value="株" '+ (data=='株'?'selected':'') +'>株</option>'
	                   +'<option value="架" '+ (data=='架'?'selected':'') +'>架</option>'
	                   +'<option value="辆" '+ (data=='辆'?'selected':'') +'>辆</option>'
	                   +'<option value="支" '+ (data=='支'?'selected':'') +'>支</option>'
	                   +'<option value="盒" '+ (data=='盒'?'selected':'') +'>盒</option>'
	                   +'<option value="卷" '+ (data=='卷'?'selected':'') +'>卷</option>'
	                   +'<option value="幅" '+ (data=='幅'?'selected':'') +'>幅</option>'
	                   +'<option value="枚" '+ (data=='枚'?'selected':'') +'>枚</option>'
	                   +'<option value="箱" '+ (data=='箱'?'selected':'') +'>箱</option>'
	                   +'<option value="对" '+ (data=='对'?'selected':'') +'>对</option>'
	                   +'<option value="双" '+ (data=='双'?'selected':'') +'>双</option>'
	                   +'<option value="串" '+ (data=='串'?'selected':'') +'>串</option>'
	                   +'<option value="个" '+ (data=='个'?'selected':'') +'>个</option>'
	                   +'</select>';
                    return str;
                }
            },
            { "data": "PACKING_AMOUNT",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="packing_amount" value="'+data+'" class="form-control" />';
                }
            },
           
            { "data": "DAMAGE_AMOUNT",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="damage_amount" disabled value="'+data+'" class="form-control" />';
                }
            },
            { "data": "UPSTREAM_SKU", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="upstream_sku" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "CUSTOM_CODE" ,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="custom_code" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "ITEM_CODE",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="item_code" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "NAME_SPECIFICATIONS",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="name_specifications" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "COLOR",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="color" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "SIZE",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="size" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "CURRENCY",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="currency" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "UNIT_VALUE",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="unit_value" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "REMARK",
            	"render": function ( data, type, full, meta ) {
            		if(!data)
            			data='';
            		return '<input type="text" name="remark" value="'+data+'" class="form-control" />';
            	}
            }
        ]
    });

    
    $('#add_cargo').on('click', function(){
        var item={};
        cargoTable.row.add(item).draw(true);
    });
    
    //刷新明细表
    gateInOrder.refleshTable = function(order_id){
    	var url = "/gateInOrder/tableList?order_id="+order_id;
    	cargoTable.ajax.url(url).load();
    }
    
    
    //通过bargode 获取相对于的 信息
    $('#'+table_name).on('keyup','input',function(e){

	    	var $self = $(this).parent().parent();
	    	var cargo_upc = $self.find('[name=cargo_upc]').val();
	    	var cargo_name = $self.find('[name=cargo_name]').val();
	    	var value = '';
	    	if(cargo_upc=='' && cargo_name==''){
	    		return false;
	    	}else if(cargo_upc != ''){
	    		value = cargo_upc;
	    	}else if(cargo_name != ''){
	    		value = cargo_name;
	    	}
    	
	    	$.post('/product/getProductByValue',{value:value},function(data){
	    		if(data.ID>0){
	    			$self.find('[name=cargo_upc]').val(data.SERIAL_NO);
	    			$self.find('[name=cargo_name]').val(data.ITEM_NAME);
	    			$self.find('[name=packing_unit]').val(data.UNIT);
	    		}else{
	    			$.scojs_message('对不起，基础信息里没有此商品，请维护', $.scojs_message.TYPE_ERROR);
	    		}
	    	})
    })
    

});   

