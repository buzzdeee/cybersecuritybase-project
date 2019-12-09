package sec.project.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private String outputLines;
 
    public StreamGobbler(InputStream inputStream, String outputLines) {
        this.inputStream = inputStream;
        this.outputLines = outputLines;
    }
 
    public String getOutputLines() {
        return this.outputLines;
    }
    
    @Override
    public void run() {
        //new BufferedReader(new InputStreamReader(inputStream)).lines()
        //  .forEach(consumer);
        InputStreamReader isr = new InputStreamReader(inputStream, 
                    StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String line;
        try {
            while ((line = br.readLine()) != null) {
                this.outputLines += line;
            }
        } catch (IOException ex) {
            Logger.getLogger(StreamGobbler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }
    

    
    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String address, Model model) {
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        Process process = null;
        if (isWindows) {
            System.out.println("starting command on windows");
            try {
                process = Runtime.getRuntime()
                        .exec(String.format("cmd.exe /c echo Welcome %s", name));
            } catch (IOException ex) {
                Logger.getLogger(SignupController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("starting command on not windows");
            try {
                process = Runtime.getRuntime()
                        .exec(String.format("sh -c echo Welcome %s", name));
            } catch (IOException ex) {
                Logger.getLogger(SignupController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String welcomemsg = "";
        StreamGobbler streamGobbler = null;
        if (process != null) {
            System.out.println("Process was not null");
            streamGobbler = 
                new StreamGobbler(process.getInputStream(), welcomemsg);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = 0;
            try {
                exitCode = process.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(SignupController.class.getName()).log(Level.SEVERE, null, ex);
            }
            assert exitCode == 0;
        }
        if (streamGobbler != null) {
            model.addAttribute("welcomemessage", streamGobbler.getOutputLines());
        }
        signupRepository.save(new Signup(name, address));
        return "done";
    }

}
