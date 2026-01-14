def call(Map config = [:]) {

    def imageName = config.imageName
    def tag       = config.tag ?: "latest"

    def dockerfileResource = ""

    // Auto-detect project type
    if (fileExists('package.json')) {
        echo "✅ ReactJS project detected"
        dockerfileResource = "reactjs/dev.Dockerfile"

    } else if (fileExists('build.gradle') || fileExists('build.gradle.kts')) {
        echo "✅ Spring Boot (Gradle) project detected"
        dockerfileResource = "spring/dev.Dockerfile"

    } else {
        error "❌ Cannot detect project type (no package.json or build.gradle found)"
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
