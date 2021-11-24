require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |spec|
  spec.name = "voxeet-uxkit-reactnative"
  spec.version = package["version"]
  spec.summary = "The Voxeet UXKit is a quick way of adding premium audio, video chats, and other supported options."
  spec.license = "Dolby Software License Agreement"
  spec.author = "Voxeet"
  spec.homepage = "https://dolby.io"
  spec.platform = :ios, "11.0"
  spec.swift_version = "5.5.1"
  spec.source = { :git => "https://github.com/voxeet/voxeet-uxkit-reactnative.git", :tag => "v#{spec.version}" }
  spec.source_files  = "ios/**/*.{h,m}"
  spec.framework = "UIKit"
  spec.dependency "React"
  spec.dependency "VoxeetUXKit", "1.6.0"
  spec.dependency "VoxeetSDK", "3.3.0"

  # MacBook arm simulator isn't supported.
  spec.pod_target_xcconfig = {
    'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64',
    'ENABLE_BITCODE' => 'NO' # Disable bitcode to support dvclient.framework dependency.
  }
  spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
end
