package nike.platform.task;

import nike.platform.monitor.MonitorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TaskController {

    @Autowired
    TaskDao taskDao;


    @Autowired
    MonitorDao monitorDao;


    @RequestMapping("task")
    public String task(Model model, @RequestParam(name = "page",defaultValue ="0" ) Integer page, @RequestParam(name = "style_color",defaultValue ="" ) String style_color) {
        if(style_color!=null&&!"".equals(style_color))
            model.addAttribute("tasks",taskDao.findAllByStyleColor(PageRequest.of(page, 100),style_color));
        else
            model.addAttribute("tasks",taskDao.findAll(PageRequest.of(page, 100) ));
        model.addAttribute("style_color",style_color);
        return "task";
    }


    /***
     * 删除操作采用同步操作，也就是直接把对应的redis也删除掉：
     * @param model
     * @param taskId
     * @return
     */
    @RequestMapping("removetask/{taskId}")
    public String removeTask(Model model, @PathVariable(name = "taskId") Long taskId) {
        Task task= taskDao.findOneById(taskId);
        //taskRedisDao.clearBuyer(task.getStyleColor(),task.getSize());
        taskDao.deleteById(taskId);
        return  "redirect:/task";
    }

    @RequestMapping("removtaskbystylecolor")
    public String removtaskbystylecolor(Model model, @RequestParam(name = "page",defaultValue ="0" ) Integer page, @RequestParam(name = "style_color",defaultValue ="" ) String style_color) {
        if(style_color!=null&&!"".equals(style_color))
        {
            taskDao.deleteByStyleColor(style_color);
            model.addAttribute("tasks",taskDao.findAllByStyleColor(PageRequest.of(page, 100),style_color));
        }
        else
        {
            model.addAttribute("tasks",taskDao.findAll(PageRequest.of(page, 100) ));
        }
        model.addAttribute("style_color",style_color);
        return "task";
    }




}
