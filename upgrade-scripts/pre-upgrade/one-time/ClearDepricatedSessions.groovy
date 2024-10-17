void call() {
    String dep = ''
    String replicas = ''
    String[] deployments = ['pgadmin-deployment', 'pgadmin-chart-deployment']

    // Try each deployment until one is found
    for (d in deployments) {
        replicas = sh(
                script: "oc get deployment ${d} -o jsonpath='{.spec.replicas}' -n $NAMESPACE || true",
                returnStdout: true
        ).trim()
        if (replicas) {
            dep = d
            break
        }
    }

    if (!dep) {
        echo "No pgadmin deployment found in namespace $NAMESPACE"
        return
    }

    // Reload pgAdmin to remove deprecated sessions
    sh "oc scale deployment/${dep} --replicas=0 -n $NAMESPACE || true"
    sh "oc scale deployment/${dep} --replicas=${replicas} -n $NAMESPACE || true"
}

return this
