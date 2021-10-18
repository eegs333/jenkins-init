pipelines = ["pipeline"]
pipelines.each { pipeline ->
    println "Creating pipeline ${pipeline}"
    create_pipeline(pipeline)
}

def create_pipeline(String name) {
    pipelineJob(name) {
        definition {
            cps {
                sandbox(true)
                script("""
def remote = [:]
remote.name = "node-1"
remote.host = "3.80.123.163"
remote.allowAnyHosts = true

node {
    withCredentials([sshUserPrivateKey(credentialsId: 'sshUser', keyFileVariable: 'identity', passphraseVariable: '', usernameVariable: 'userName')]) {
        remote.user = userName
        remote.identityFile = identity
        
        stage("Git clone") {
            dir("django-code") {
                git url: 'https://github.com/eegs333/django-code.git', branch: 'master'    
            }
            sshPut remote: remote, from: 'django-code', into: '.'
        }
        
        stage("build") {
            sshCommand remote: remote, command: 'cd django-code; if [ ! -d "data" ]; then make build; fi'
        }
        
        stage("migrate") {
            sshCommand remote: remote, command: 'cd django-code; if [ ! -d "data" ]; then make migrate; fi'
        }
        
        stage("superuser") {
            sshCommand remote: remote, command: 'cd django-code; make superuser'
        }
        
        stage("deploy") {
            sshCommand remote: remote, command: 'cd django-code; make up ARG=-d '
        }
        
        stage("test") {
            sshCommand remote: remote, command: 'cd django-code; make test'
        }
        
    }
}
""")
            }
        }
    }
}
