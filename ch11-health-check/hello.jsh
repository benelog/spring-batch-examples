//usr/bin/env jshell --add-exports jdk.jconsole/sun.tools.jconsole --show-version "$0" "$@"; exit $?

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import sun.tools.jconsole.LocalVirtualMachine;

int getPid(String keyword) {
    Map<Integer, LocalVirtualMachine> machines = LocalVirtualMachine.getAllVirtualMachines();

    Optional<Entry<Integer, LocalVirtualMachine>> process = machines.entrySet().stream()
        .filter(et -> et.getValue().toString().contains(keyword))
        .findFirst();

    if (process.isPresent()) {
        return process.get().getKey();
    }
    throw new IllegalStateException("cannot find process by " + keyword);
}

JMXConnector connect(int pid) throws IOException {
    LocalVirtualMachine vm = LocalVirtualMachine.getLocalVirtualMachine(pid);
    vm.startManagementAgent();
    String connectorAddress = vm.connectorAddress();
    var jmxUrl = new JMXServiceURL(connectorAddress);
    return JMXConnectorFactory.connect(jmxUrl);
}

void run(int pid, String beanName, String operation) {
    try (JMXConnector connector = connect(pid)) {
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        connection.invoke(new ObjectName(beanName), operation, new Object[0], new String[0]);
    } catch (Exception ex) {
        throw new RuntimeException("fail to execute " + operation, ex);
    }
}

int pid = getPid("HealthCheckApplication"); // ps -ef | grep 으로 해당 프로세스를 잡을 수 있는 키워드를 넣는다.
System.out.println("PID : " + pid);

String job = System.getProperty("job");
String beanName = "kr.co.wikibook.batch.healthcheck:type=JmxTest,name=jmxTest";
String operation = "test";
run(pid, beanName, operation);
System.out.println(job);
System.out.println(operation + " operation executed");
/exit
