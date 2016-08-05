
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;

    $('#menu_incoming').addClass('active').find('ul').addClass('in');
    
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

        var items_array = inspectionOrder.buildItemDetail();
        var order = {
            id: $('#order_id').val(),
            gate_in_order_id: $('#gate_in_order_id').val(),
            warehouse_id: $('#warehouse_id').val(),  
            warehouse_area: $('#warehouse_area').val(),  
            inspection_packing_unit: $('#inspection_packing_unit').val(),  
            shipper: $('#shipper').val(),
            can_check: $('#can_check').val(),
            have_check: $('#have_check').val(),  
            difference: $('#difference').val(),  
            status: $('#status').val()==''?'新建':$('#status').val(),  
            remark: $('#remark').val(),
            item_list:items_array
        };

        var status = $('#status').val();
        //异步向后台提交数据
        $.post('/inspectionOrder/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){
            	$("#creator_name").val(order.CREATOR_NAME);
                $("#create_stamp").val(order.CREATE_STAMP);
                $("#order_id").val(order.ID);
                $("#status").val(order.STATUS);
                contactUrl("edit?id",order.ID);
                $.scojs_message('保存成功', $.scojs_message.TYPE_OK);
                $('#saveBtn').attr('disabled', false);
                
                //异步刷新明细表
                inspectionOrder.refleshTable(order.ID);
            }else{
                $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
                $('#saveBtn').attr('disabled', false);
            }
        },'json').fail(function() {
            $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
            $('#saveBtn').attr('disabled', false);
          });
    });  
 
    //按钮控制
    var order_id = $("#order_id").val()
    if(order_id != ''){
    	 $('#submitDingDanBtn').attr('disabled',false);
    }
    
    
 

    $("#have_check").on("input",function(){
    	var can_check = $("#can_check").val()==''?'0':$("#can_check").val();
		var have_check =  $("#have_check").val()==''?'0':$("#have_check").val();
		if(parseFloat(can_check) >= parseFloat(have_check)){
			$("#difference").val(parseFloat(can_check) - parseFloat(have_check));
		}else{
			$.scojs_message('抱歉！已验数量不可以大于可验数量！！！', $.scojs_message.TYPE_ERROR);
			$("#have_check").val(can_check);
			$("#difference").val('0');
		}  	
	 });
	
    
    $('#gate_in_id_list').on('click',function(){
    	$.post('/inspectionOrder/queryAmount',{gate_in_id:$('#gate_in_id').val()},function(data){
    		if(data)
    			$('#can_check').val(data.AMOUNT);
    	})
    })
	

} );