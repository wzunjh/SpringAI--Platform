package com.example.springai.tools;


import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.example.springai.entity.po.Course;
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


    private ICourseService iCourseService;
    private ICourseReservationService iCourseReservationService;
    private ISchoolService iSchoolService;


    @Tool(description = "根据条件查询课程")
    public List<Course> quaryCourse(@ToolParam(description = "查询条件") CourseQuery query) {
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


}
