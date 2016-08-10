
$(document).ready(function() {

    var deletedTableIds=[];

    //删除一行
    $("#cargo_table").on('click', '.delete', function(e){
        e.preventDefault();
        var tr = $(this).parent().parent();
        deletedTableIds.push(tr.attr('id'))
        
        cargoTable.row(tr).remove().draw();
    }); 
    

    itemOrder.buildItemDetail=function(){
        var item_table_rows = $("#cargo_table tr");
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
    

    //------------事件处理
    var cargoTable = $('#cargo_table').DataTable({
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
        },
        "columns": [
            { "width": "30px",
                "render": function ( data, type, full, meta ) {
                	return '<button type="button" class="delete btn btn-default btn-xs">删除</button> ';
                }
            },
            { "data": "BAR_CODE", "width": "120px",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="bar_code" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "CARGO_NAME", "width": "180px",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="cargo_name" value="'+data+'" class="form-control check" />';
                }
            },
            { "data": "PACKING_AMOUNT",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="packing_amount" value="'+data+'" class="form-control check" />';
                }
            },
            { "data": "SHELVES", "width": "100px","visible":false,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="shelves" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "CARTON_NO", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="carton_no" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "UPSTREAM_SKU", "visible":false,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="upstream_sku" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "CUSTOM_CODE" ,"visible":false,
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
            { "data": "CURRENCY","visible":false,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="currency" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "UNIT_VALUE","visible":false,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="unit_value" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "PACKING_UNIT","width": "60px",
                "render": function ( data, type, full, meta ) {
                	if(!data)
                        data='';
                    var str= '<select class="form-control search-control" name="packing_unit">'
            	   	   +'<option></option>'
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
            }
        ]
    });

    $('#add_cargo').on('click', function(){
        var item={};
        cargoTable.row.add(item).draw(true);
    });
    
    //刷新明细表
    itemOrder.refleshTable = function(order_id){
    	var url = "/gateOutOrder/tableList?order_id="+order_id;
    	cargoTable.ajax.url(url).load();
    }
    
    //库存货品校验
    $('#cargo_table').on('blur','.check',function(){
    	var $self = $(this).parent().parent();
    	var name = $self.find('[name=cargo_name]').val();
    	var amount = $self.find('[name=packing_amount]').val();
    	if(name=='')
    		return false;
    	
    	$.post('/inventory/check',{name:name,amount:amount},function(data){
    		if(data.RESULT!="ok"){
    			$.scojs_message(data.RESULT, $.scojs_message.TYPE_ERROR);
    		}else{
    			debugger;
    			$self.find('[name=packing_unit]').val(data.ORDER.UNIT);
    			$self.find('[name=bar_code]').val(data.ORDER.CARGO_BARCODE);
    			$self.find('[name=shelves]').val(data.ORDER.SHELVES);
    		}
    	})
    	
    })
    
    
});