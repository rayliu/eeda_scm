
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
            var id = $(row).attr('id');
            if(!id){
                id='';
            }

            var item={
                id: id,
                item_no: $(row.children[1]).find('input').val(), 
                qty: $(row.children[2]).find('input').val(),
                unit: $(row.children[3]).find('input').val(),
                price: $(row.children[4]).find('input').val(),
                total: $(row.children[5]).find('input').val(),
                action: $('#order_id').val().length>0?'UPDATE':'CREATE'
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
        return cargo_items_array;
    };

    salesOrder.reDrawCargoTable=function(order){
        deletedTableIds=[];
        cargoTable.clear();
        for (var i = 0; i < order.ITEM_LIST.length; i++) {
            var item = order.ITEM_LIST[i];
            var item={
                "ID": item.ID,
                "ITEM_NO": item.ITEM_NO,
                "STY": item.STY,
                "UNIT": item.UNIT,
                "PRICE": item.PRICE,
                "TOTAL": item.TOTAL
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
            { "data": "ORDER_NO", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" disabled value="'+data+'" class="form-control"/>';
                }
            },
            { "data": "STY" ,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" disabled value="'+data+'" class="form-control"/>';
                }
            },
            { "data": "UNIT",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    var str= '<select class="form-control search-control" name="unit">'
              	   	   +'<option></option>'
              	   	   +'<option value="6" '+ (data=='6'?'selected':'') +'>套</option>'
  	                   +'<option value="7" '+ (data=='7'?'selected':'') +'>个</option>'
  	                   +'<option value="8" '+ (data=='8'?'selected':'') +'>只</option>'
  	                   +'<option value="11" '+ (data=='11'?'selected':'') +'>件</option>'
  	                   +'<option value="12" '+ (data=='12'?'selected':'') +'>支</option>'
  	                   +'<option value="14" '+ (data=='14'?'selected':'') +'>根</option>'
  	                   +'<option value="15" '+ (data=='15'?'selected':'') +'>条</option>'
  	                   +'<option value="17" '+ (data=='17'?'selected':'') +'>块</option>'
  	                   +'<option value="18" '+ (data=='18'?'selected':'') +'>卷</option>'
  	                   +'<option value="20" '+ (data=='20'?'selected':'') +'>片</option>'
  	                   +'<option value="35" '+ (data=='35'?'selected':'') +'>千克</option>'
  	                   +'<option value="36" '+ (data=='36'?'selected':'') +'>克</option>'
  	                   +'<option value="70" '+ (data=='70'?'selected':'') +'>吨</option>'
  	                   +'<option value="75" '+ (data=='75'?'selected':'') +'>斤</option>'
  	                   +'<option value="96" '+ (data=='96'?'selected':'') +'>毫升</option>'
  	                   +'<option value="122" '+ (data=='122'?'selected':'') +'>罐</option>'
  	                   +'<option value="125" '+ (data=='125'?'selected':'') +'>包</option>'
  	                   +'<option value="134" '+ (data=='134'?'selected':'') +'>枚</option>'
  	                   +'<option value="136" '+ (data=='136'?'selected':'') +'>袋</option>'
  	                   +'<option value="139" '+ (data=='139'?'selected':'') +'>粒</option>'
  	                   +'<option value="140" '+ (data=='140'?'selected':'') +'>盒</option>'
  	                   +'<option value="142" '+ (data=='142'?'selected':'') +'>瓶</option>'
 	                   +'</select>';
                      return str;
                }
            },
            { "data": "PRICE",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" disabled value="'+data+'" class="form-control"/>';
                }
            },
            { "data": "TOTAL",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" disabled value="'+data+'" class="form-control"/>';
                }
            }
        ]
    });

    $('#add_cargo').on('click', function(){
        var item={};
        cargoTable.row.add(item).draw(true);
    });

    
} );