
$(document).ready(function() {

    var deletedTableIds=[];

    //删除一行
    $("#cargo_table").on('click', '.delete', function(e){
        e.preventDefault();
        var tr = $(this).parent().parent();
        deletedTableIds.push(tr.attr('id'))
        
        cargoTable.row(tr).remove().draw();
    }); 

    gateInOrder.buildCargoDetail=function(){
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
                number: $(row.children[1]).find('input').val(), 
                carton_no: $(row.children[2]).find('input').val(), 
                upstream_sku: $(row.children[3]).find('input').val(), 
                custom_code: $(row.children[4]).find('input').val(),
                item_code: $(row.children[5]).find('input').val(),
                name_specifications: $(row.children[6]).find('input').val(),
                color: $(row.children[7]).find('input').val(),
                size: $(row.children[8]).find('input').val(), 
                currency: $(row.children[9]).find('input').val(), 
                unit_value: $(row.children[10]).find('input').val(),
                packing_unit: $(row.children[11]).find('input').val(), 
                packing_amount: $(row.children[12]).find('input').val(), 
                received_amount: $(row.children[13]).find('input').val(), 
                damage_amount: $(row.children[14]).find('input').val(), 
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
    
    gateInOrder.reDrawCargoTable=function(order){
        deletedTableIds=[];
        cargoTable.clear();
        for (var i = 0; i < order.ITEM_LIST.length; i++) {
            var item = order.ITEM_LIST[i];
            var item={
                "ID": item.ID,
                "NUMBER": item.NUMBER,
                "CARTON_NO": item.CARTON_NO,
                "UPSTREAM_SKU": item.UPSTREAM_SKU,
                "CUSTOM_CODE": item.CUSTOM_CODE,
                "ITEM_CODE": item.ITEM_CODE,
                "NAME_SPECIFICATIONS": item.NAME_SPECIFICATIONS,
                "COLOR": item.COLOR,
                "SIZE": item.SIZE,
                "CURRENCY": item.CURRENCY,
                "UNIT_VALUE": item.UNIT_VALUE,
                "PACKING_UNIT": item.PACKING_UNIT,
                "PACKING_AMOUNT": item.PACKING_AMOUNT,
                "RECEIVED_AMOUNT": item.RECEIVED_AMOUNT,
                "DAMAGE_AMOUNT": item.DAMAGE_AMOUNT
            };
    
            cargoTable.row.add(item).draw(false);
        }       
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
            { "data": "NUMBER", 
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
            { "data": "PACKING_UNIT",
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
            { "data": "RECEIVED_AMOUNT",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "DAMAGE_AMOUNT",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" value="'+data+'" class="form-control" required/>';
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

    
} );