package io.metersphere.plan.controller;

import io.metersphere.plan.domain.TestPlan;
import io.metersphere.plan.service.TestPlanService;
import io.metersphere.validation.groups.Created;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-plan")
public class TestPlanController {
    @Resource
    private TestPlanService testPlanService;

    @PostMapping("/add")
    public boolean addUser(@Validated({Created.class}) @RequestBody TestPlan testPlan) {
        return testPlanService.add(testPlan);
    }
}