
$(document).ready(function() {

    var deletedTableIds=[];

    //删除一行
    $("#cargo_table").on('click', '.delete', function(e){
        e.preventDefault();
        var tr = $(this).parent().parent();
        deletedTableIds.push(tr.attr('id'))
        
        cargoTable.row(tr).remove().draw();
    }); 

    salesOrder.buildCargoDetail=function(){
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
                item_name: $(row.children[1]).find('input').val(), 
                item_desc: $(row.children[2]).find('input').val(), 
                item_no: $(row.children[3]).find('input').val(), 
                cus_item_no: $(row.children[4]).find('input').val(), 
                qty: $(row.children[5]).find('input').val(),
                unit: $(row.children[6]).find('input').val(),
                price: $(row.children[7]).find('input').val(),
                total: $(row.children[8]).find('input').val(),
                gift_flag: $(row.children[9]).find('input').val(), 
                currency: $(row.children[10]).find('input').val(), 
                action: id!=''?'UPDATE':'CREATE'
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

    salesOrder.reDrawCargoTable=function(order){
        deletedTableIds=[];
        cargoTable.clear();
        for (var i = 0; i < order.ITEM_LIST.length; i++) {
            var item = order.ITEM_LIST[i];
            var item={
                "ID": item.ID,
                "ITEM_NAME": item.ITEM_NAME,
                "ITEM_DESC": item.ITEM_DESC,
                "ITEM_NO": item.ITEM_NO,
                "CUS_ITEM_NO": item.CUS_ITEM_NO,
                "STY": item.STY,
                "UNIT": item.UNIT,
                "PRICE": item.PRICE,
                "TOTAL": item.TOTAL,
                "GIFT_FLAG": item.GIFT_FLAG,
                "CURRENCY": item.CURRENCY
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
        "autoWidth": true,
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
            { "data": "ITEM_NAME", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="item_name" value="'+data+'" class="form-control"/>';
                }
            },
            { "data": "ITEM_DESC", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="item_desc" value="'+data+'" class="form-control"/>';
                }
            },
            { "data": "ITEM_NO", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='ISQHDF9312146008460';
                    return '<input type="text" name="item_no" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "CUS_ITEM_NO", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="cus_item_no" value="'+data+'" class="form-control"/>';
                }
            },
            { "data": "QTY" ,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='1';
                   return '<input type="number" name="qty" value="'+data+'" class="form-control calculate" required/>';
                }
            },
            { "data": "UNIT",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='001';
                   return '<input type="text" name="unit" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "PRICE",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='0';
                   return '<input type="text" name="price" value="'+data+'" class="form-control calculate" required/>';
                }
            },
            { "data": "TOTAL",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='0';
                   return '<input type="text" name="total" value="'+data+'" class="form-control calculate" required/>';
                }
            },
            { "data": "GIFT_FLAG",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='1';
                   return '<input type="text" name="gift_flag" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "CURRENCY",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='142';
                   return '<input type="text" name="item_currency" value="'+data+'" class="form-control" required/>';
                }
            }
        ]
    });
    
    $('#cargo_table').on('input','.calculate',function(){
    	var row = $(this).parent().parent();
    	var sty = $(row.find('.calculate')[0]).val();
    	var price = $(row.find('.calculate')[1]).val();
    	var total = $(row.find('.calculate')[2]).val(parseFloat(sty)*parseFloat(price));
    })

    $('#add_cargo').on('click', function(){
        var item={};
        cargoTable.row.add(item).draw(false);
    });
    
  //刷新明细表
    salesOrder.refleshItemTable = function(order_id){
    	var url = "/salesOrder/tableList?order_id="+order_id
        +"&table_type=item";
    	cargoTable.ajax.url(url).load();
    }

    
} );