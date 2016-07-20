
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
            warehouse_area: $('#warehouse_area').val(),  
            warehouse_road: $('#warehouse_road').val(),  
            warehouse_row: $('#warehouse_row').val(),
            warehouse_column: $('#warehouse_column').val(),
            warehouse_layer: $('#warehouse_layer').val(),   
            position: $('#position').val()
        };

        //异步向后台提交数据
        $.post('/warehouseShelves/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){	
            	$("#creator_name").val(data.CREATOR_NAME);
                $("#create_stamp").val(order.CREATE_STAMP);
                $("#order_id").val(order.ID);
                contactUrl("edit?id",order.ID);
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