
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;

    $('#menu_profile').addClass('active').find('ul').addClass('in');
    
    $('#amount').blur(function(){
        $('#total_amount').text($(this).val());
    });

    //------------save
    $('#saveBtn').click(function(e){
        //阻止a 的默认响应行为，不需要跳转
        e.preventDefault();
        //提交前，校验数据
        if(!$("#orderForm").valid()){
            return;
        }
        $(this).attr('disabled', true);

        var order = {
            id: $('#order_id').val(),
            warehouse_id: $('#warehouse_id').val(),  
            warehouse_area_begin: $('#warehouse_area_begin').val(),  
            warehouse_area_end: $('#warehouse_area_end').val(),  
            warehouse_road_begin: $('#warehouse_road_begin').val(),
            warehouse_road_end: $('#warehouse_road_end').val(),
            warehouse_row_begin: $('#warehouse_row_begin').val(),
            warehouse_row_end: $('#warehouse_row_end').val(),
            warehouse_column_begin: $('#warehouse_column_begin').val(),
            warehouse_column_end: $('#warehouse_column_end').val(),
            warehouse_layer_begin: $('#warehouse_layer_begin').val(),   
            warehouse_layer_end: $('#warehouse_layer_end').val(),
            position: $('#position').val()
        };

        //异步向后台提交数据
        $.post('/warehouseShelves/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){	
            	$("#creator_name").val(data.CREATOR_NAME);
                $("#create_stamp").val(order.CREATE_STAMP);
                $("#order_id").val(order.ID);
                eeda.contactUrl("edit?id",order.ID);
                $.scojs_message('保存成功', $.scojs_message.TYPE_OK);

                $('#saveBtn').attr('disabled', false);       
            }else{
                $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
                $('#saveBtn').attr('disabled', false);
            }
        },'json').fail(function() {
            $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
            $('#saveBtn').attr('disabled', false);
          });
    });
    
} );