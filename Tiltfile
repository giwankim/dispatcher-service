custom_build(
  ref = 'dispatcher-service',
  command = './gradlew bootBuildImage --imageName $EXPECTED_REF',
  deps = ['build.gradle.kts', 'src']
)

k8s_yaml(['k8s/deployment.yaml', 'k8s/service.yaml'])

k8s_resource('dispatcher-service', port_forwards=['9003'])
