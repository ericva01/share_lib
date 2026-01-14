def call(Map config = [:]) {

    def imageName = config.imageName
    def tag       = config.tag ?: "latest"

    def dockerfileResource = ""

    // Auto-detect project type
    if (fileExists('pom.xml') || fileExists('build.gradle') || fileExists('build.gradle.kts')) {
        echo "✅ Spring Boot project detected"
        dockerfileResource = "spring/dev.Dockerfile"

    } else if (fileExists('package.json')) {
        echo "✅ ReactJS project detected"
        dockerfileResource = "reactjs/dev.Dockerfile"

    } else {
        error "❌ Cannot detect project type"
    }

    // Load Dockerfile from shared library resources
    writeFile(
        file: "Dockerfile",
        text: libraryResource(dockerfileResource)
    )

    // Build Docker image
    sh """
        docker build -t ${imageName}:${tag} .
    """
}
