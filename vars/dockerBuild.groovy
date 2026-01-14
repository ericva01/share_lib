def call(Map config = [:]) {

    def imageName = config.imageName
    def tag       = config.tag ?: "latest"

    def dockerfileResource = ""

    if (fileExists('package.json')) {
        echo "Detected ReactJS project"
        dockerfileResource = "reactjs/dev.Dockerfile"

    } else if (fileExists('pom.xml')) {
        echo "Detected Spring Boot project"
        dockerfileResource = "spring/dev.Dockerfile"

    } else {
        error "Cannot detect project type (no package.json or pom.xml found)"
    }

    // Load Dockerfile from shared library resources
    writeFile(
        file: "Dockerfile",
        text: libraryResource(dockerfileResource)
    )

    sh """
        docker build -t ${imageName}:${tag} .
    """
}
