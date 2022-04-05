require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |spec|
  spec.name = "voxeet-uxkit-reactnative"
  spec.version = package["version"]
  spec.summary = "The Voxeet UXKit is a quick way of adding premium audio, video chats, and other supported options."
  spec.license = "Dolby Software License Agreement"
  spec.author = "Voxeet"
  spec.homepage = "https://dolby.io"
  spec.platform = :ios, "12.0"
  spec.source = { :git => "https://github.com/voxeet/voxeet-uxkit-reactnative.git", :tag => "v#{spec.version}" }
  spec.source_files  = "ios/**/*.{h,m}"
  spec.framework = "UIKit"
  spec.dependency "React"
  spec.dependency "VoxeetUXKit", "~>1.0"

  spec.pod_target_xcconfig = {
    'ENABLE_BITCODE' => 'NO' # Disable bitcode to support dvclient.framework dependency.
  }
end
