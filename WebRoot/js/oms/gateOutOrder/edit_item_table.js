
$(document).ready(function() {

    var deletedTableIds=[];

    //删除一行
    $("#cargo_table").on('click', '.delete', function(e){
        e.preventDefault();
        var tr = $(this).parent().parent();
        deletedTableIds.push(tr.attr('id'))
        
        cargoTable.row(tr).remove().draw();
    }); 
    
    gateOutOrder.buildCargoDetail=function(){
        var cargo_table_rows = $("#cargo_table tr");
        var cargo_items_array=[];
        for(var index=0; index<cargo_table_rows.length; index++){
            if(index==0)
                continue;

            var row = cargo_table_rows[index];
            var empty = $(row).find('.dataTables_empty').text();
            if(empty)
            	continue;
            
            var id = $(row).attr('id');
            if(!id){
                id='';  
            }
            var item={
                id: id,
                bar_code: $(row.children[1]).find('input').val(), 
                cargo_name: $(row.children[2]).find('input').val(), 
                packing_amount: $(row.children[3]).find('input').val(),
                express_no: $(row.children[4]).find('input').val(),
                carton_no: $(row.children[5]).find('input').val(), 
                upstream_sku: $(row.children[6]).find('input').val(), 
                custom_code: $(row.children[7]).find('input').val(),
                item_code: $(row.children[8]).find('input').val(),
                name_specifications: $(row.children[9]).find('input').val(),
                color: $(row.children[10]).find('input').val(),
                size: $(row.children[11]).find('input').val(), 
                currency: $(row.children[12]).find('input').val(), 
                unit_value: $(row.children[13]).find('input').val(),
                packing_unit: $(row.children[14]).find('select').val(), 
                action: id.length>0?'UPDATE':'CREATE'
            };
            cargo_items_array.push(item);
            
        }

        //add deleted items
        for(var index=0; index<deletedTableIds.length; index++){
            var id = deletedTableIds[index];
            var item={
                id: id,
                action: 'DELETE'
            };
            cargo_items_array.push(item);
        }
        deletedTableIds = [];
        return cargo_items_array;
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
                    return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "CARGO_NAME", "width": "200px",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "PACKING_AMOUNT",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "EXPRESS_NO", "width": "120px", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "CARTON_NO", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "UPSTREAM_SKU", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "CUSTOM_CODE" ,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "ITEM_CODE",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "NAME_SPECIFICATIONS",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "COLOR",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "SIZE",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "CURRENCY",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "UNIT_VALUE",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "PACKING_UNIT","width": "60px",
                "render": function ( data, type, full, meta ) {
                	if(!data)
                        data='';
                    var str= '<select class="form-control search-control">'
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
    gateOutOrder.refleshTable = function(order_id){
    	var url = "/gateOutOrder/tableList?order_id="+order_id;
    	cargoTable.ajax.url(url).load();
    }

    
});