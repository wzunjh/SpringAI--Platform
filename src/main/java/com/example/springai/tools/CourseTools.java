package com.example.springai.tools;


import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.example.springai.entity.po.Course;
import com.example.springai.entity.po.CourseReservation;
import com.example.springai.entity.po.School;
import com.example.springai.entity.query.CourseQuery;
import com.example.springai.service.ICourseReservationService;
import com.example.springai.service.ICourseService;
import com.example.springai.service.ISchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;


@RequiredArgsConstructor
@Component
public class CourseTools {


    private final ICourseService iCourseService;
    private final ICourseReservationService iCourseReservationService;
    private final ISchoolService iSchoolService;


    @Tool(description = "根据条件查询课程")
    public List<Course> quaryCourse(@ToolParam(description = "查询条件",required = false) CourseQuery query) {
        if (query == null) {
            return iCourseService.list();
        }

        QueryChainWrapper<Course> wrapper = iCourseService.query()
                .eq(query.getType() != null, "type", query.getType())
                .le(query.getEdu() != null, "edu", query.getEdu());

        if (query.getSorts() != null && !query.getSorts().isEmpty()) {

            for (CourseQuery.Sort sort : query.getSorts()) {
                wrapper.orderBy(true, sort.getAsc(), sort.getField());
            }
        }

        return wrapper.list();
    }

    @Tool(description = "查询所有校区")
    public List<School> quarySchool(){
        return iSchoolService.list();
    }


    @Tool(description = "新增预约订单,返回订单号")
    public Integer creatCourseReservation(
            @ToolParam(description = "预约课程") String course,
            @ToolParam(description = "学生姓名") String studentName,
            @ToolParam(description = "联系方式") String contactInfo,
            @ToolParam(description = "预约校区") String school,
            @ToolParam(description = "备注",required = false) String remark
    ){
        CourseReservation reservation = new CourseReservation();
        reservation.setCourse(course);
        reservation.setStudentName(studentName);
        reservation.setContactInfo(contactInfo);
        reservation.setSchool(school);
        reservation.setRemark(remark);
        iCourseReservationService.save(reservation);

        return reservation.getId();
    }

}
