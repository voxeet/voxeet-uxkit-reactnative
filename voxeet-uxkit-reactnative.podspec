require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name = "voxeet-uxkit-reactnative"
  s.version = package["version"]
  s.summary = "The Voxeet UXKit is a quick way of adding premium audio, video chats, and other supported options."
  s.license = "MIT"
  s.author = "Voxeet"
  s.homepage = "https://voxeet.com"
  s.platform = :ios, "9.0"
  s.swift_version = "5.1.3"
  s.source = { :git => "https://github.com/voxeet/voxeet-uxkit-ios.git", :tag => s.version }
  s.source_files  = "ios/**/*.{h,m}"
  s.framework = "UIKit"
  s.dependency "React"
  s.dependency "VoxeetConferenceKit"
end
